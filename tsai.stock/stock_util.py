
import os
import time
from pathlib import Path
from urllib.parse import urljoin


import FinanceDataReader as fdr
import numpy as np
import pandas as pd
import pandas_ta as ta
import parmap
import requests
from pykrx import stock
from sklearn.metrics import classification_report
from tqdm import tqdm

from tsai.all import *

from datetime import datetime, timedelta
from dateutil.relativedelta import relativedelta

my_setup()

sampleListKey=["X","y","trainIndex","testIndex","totalNum","ticker","yDate", "unlabelX","unlabelTicker","unlabelDate"]



def creaetDir(baseDir,dateEnd,dayAbove):
    baseDir=Path(baseDir)
    csvPath=baseDir/'data/'
    pykrxPath=csvPath/"pykrx"
    preprocess1Path=baseDir/f"preprocess1_date{dateEnd}"
    preprocess2Path=baseDir/f"preprocess2_date{dateEnd}_day{dayAbove}"
    os.makedirs(csvPath, exist_ok = True) 
    os.makedirs(pykrxPath, exist_ok = True) 
    os.makedirs(preprocess1Path, exist_ok = True) 
    os.makedirs(preprocess2Path, exist_ok = True) 
    
    return [pykrxPath, preprocess1Path, preprocess2Path]
    

    
    
    
    

def downloadCsv(code,dateStart,dateEnd,savePath):
    file=savePath/(code+'.csv')
    try:
        df=stock.get_market_ohlcv_by_date(dateStart, dateEnd, code)
        if(len(df)==0): return
        tempDF=pd.DataFrame()
        tempDF[['Close','Open','High','Low','Volume']]=df[['종가','시가','고가','저가','거래량']]
        tempDF.to_csv(file) 
        time.sleep(1)
    except Exception as e:   
        print( e)
        print(code)
def downloadCsvPykrxAll(dateStart,dateEnd,savePath):
    print("csv download")
    dateStart=dateStart.replace("-", "")
    dateEnd=dateEnd.replace("-", "")
    
    KosdaqTickers = stock.get_market_ticker_list(dateEnd, market="KOSDAQ")
    KospiTickers = stock.get_market_ticker_list(dateEnd, market="KOSPI")
    pykrx_tickers=KospiTickers
    print(len(pykrx_tickers))
    print(pykrx_tickers[:10])
    result=parmap.map(downloadCsv, pykrx_tickers, dateStart,dateEnd,savePath, pm_pbar=True)

    

    
    
def preprocess1(trainDateStart,testDateStop,csvPath,preprocess1Path):
    # cut date and filtering
    print("preprocess1")    
    tickers = stock.get_market_ticker_list(testDateStop, market="KOSPI")
    for ticker in tqdm(tickers):
        file= csvPath/(ticker+'.csv')
        if not file.exists(): continue

        #get ohlcv
        df = pd.read_csv(file,index_col=0)
        df=df[df['Volume']!=0]
        df=df[df['Open']!=0]
        df=df[df['High']!=0]
        df=df[df['Low']!=0]
        df=df[df['Close']!=0]
        df=df.loc[trainDateStart:]
        df=df.loc[:testDateStop]  
        df=df[['Close','Open','High','Low','Volume']]
        df.index=pd.to_datetime(df.index)
        if len(df)<50: continue  
        if np.max(df.Close.pct_change(1))>0.30 or np.min(df.Close.pct_change(1))<-0.30: continue    #너무 확움직이는거 제외
        # if (df.Volume * df.Close)[-50:].mean() <400000000: continue #거래량 많은거 4억원이상
        # if df.Close[-50:].mean() <1000:continue   #동전주 스킵   

        df.to_csv(preprocess1Path/(ticker+'.csv')) 
    

    
    

def getPreprocess2Dict(ticker,trainDateStart,trainDateStop,testDateStop,predictDayAbove,windowLength,preprocess1Path,preprocess2Path):
    
    sampleListSingleDict={key:np.array([]) for key in sampleListKey}
    file=preprocess1Path/(ticker+".csv")
    if not file.exists():
        return sampleListSingleDict
    df = pd.read_csv(file,index_col=0)
    

    #stock indicator
    df.ta.sma( append=True, length=5)
    df.ta.sma( append=True, length=15)
    df.ta.sma( append=True, length=60)
    df.ta.bbands( append=True)
    df.ta.macd( append=True)
    df.ta.kdj( append=True)
    df.ta.rsi( append=True)
    df.ta.cci( append=True)
    df.ta.ema( append=True)
    df.ta.roc( append=True)
    df.ta.stochrsi( append=True)
    df.ta.atr( append=True)
    df.ta.ema( append=True, length=20)

    #drop na data, change type to float32
    df['labels'] = df.Close.pct_change(predictDayAbove)
    df = df.dropna()    
    df = df.astype(np.float32)
    df.index=pd.to_datetime(df.index)

    # window split and train/test split
    dfLen=len(df.loc[:testDateStop]  )-windowLength-predictDayAbove+1
    if dfLen>0:
        # train test split index calculate
        dfTrainLen=max(0,len(df.loc[:trainDateStop]  )-windowLength-predictDayAbove+1)
        dfTestLen=dfLen-dfTrainLen
        trainIndex=np.arange(dfTrainLen)
        testIndex=np.arange(dfTrainLen,dfLen)

        X, y = SlidingWindow(windowLength, stride=1, horizon=[predictDayAbove], get_x=df.columns, get_y='labels')(df)
        unlabelX, _ = SlidingWindow(windowLength, stride=1, horizon=[predictDayAbove], get_x=df.columns, get_y=[],start=dfLen)(df)

        sampleListSingleDict["X"]=X
        sampleListSingleDict["y"]=y        
        sampleListSingleDict["trainIndex"]=trainIndex
        sampleListSingleDict["testIndex"]=testIndex
        sampleListSingleDict["ticker"]=[ticker]*len(y)
        sampleListSingleDict["yDate"]=df.index.strftime("%Y-%m-%d").tolist()[-len(y):]
        sampleListSingleDict["unlabelX"]= unlabelX 
        sampleListSingleDict["unlabelTicker"]=[ticker]*predictDayAbove
        sampleListSingleDict["unlabelDate"]=pd.date_range(df.index[-1], periods=predictDayAbove+1).format(formatter=lambda x: x.strftime('%Y-%m-%d'))[1:]

    return sampleListSingleDict


def preprocess2(trainDateStart,trainDateStop,testDateStop,predictDayAbove,windowLength,preprocess1Path,preprocess2Path):
    # concat all tickers as one npy
    # populate columns
    print("preprocess2")

        
    # multiprocessing, loop all csv to get X y
    tickers = stock.get_market_ticker_list(testDateStop, market="KOSPI")
    result=parmap.map(getPreprocess2Dict, tickers,trainDateStart,trainDateStop,testDateStop,predictDayAbove,windowLength,preprocess1Path,preprocess2Path, pm_pbar=True)


    # adjust train index and test index
    prevIndexEnd=0
    for i in range(len(result)):
        result[i]["trainIndex"]=result[i]["trainIndex"]+prevIndexEnd
        result[i]["testIndex"]=result[i]["testIndex"]+prevIndexEnd
        prevIndexEnd+=len(result[i]["trainIndex"])+len(result[i]["testIndex"])

        
    # concat as one big list
    sampleListDictConcat=dict({})
    for key in sampleListKey:
        try:
            sampleListDictConcat[key]=np.concatenate([item[key] for item in result if len(item[key])!=0],axis=0)
        except:
            sampleListDictConcat[key]=[]
        
    # save npy
    for key in sampleListDictConcat:
        np.save(preprocess2Path/f'pykrx_1_{key}.npy', sampleListDictConcat[key])


        
def predict(preprocess2Path,modelPath,dateStart):
    # #npy load
    sampleListKey=["X","y","trainIndex","testIndex","ticker","yDate", "unlabelX","unlabelTicker","unlabelDate"]
    sampleListDict={key:[] for key in sampleListKey}
    for key in sampleListKey:
        sampleListDict[key] = np.load(preprocess2Path/f'pykrx_1_{key}.npy')


    #splits
    sampleListDict["testIndex"]=list(sampleListDict["testIndex"])
    sampleListDict["trainIndex"]=list(sampleListDict["trainIndex"])
    splits=[sampleListDict["trainIndex"],sampleListDict["testIndex"]]

    # X y
    baseNum = 0.002
    X = sampleListDict["X"]
    y = sampleListDict["y"]
    y_cat = sampleListDict["y"] > baseNum



    #test
    XTest=sampleListDict["X"][sampleListDict["testIndex"]]
    yTest=sampleListDict["y"][sampleListDict["testIndex"]]
    tickerTest=sampleListDict["ticker"][sampleListDict["testIndex"]]
    dateTest=sampleListDict["yDate"][sampleListDict["testIndex"]]
    lastClosePriceTest=XTest[:,0,-1]

    #unlabel
    XUnlabel=sampleListDict["unlabelX"]
    tickerUnlabel=sampleListDict["unlabelTicker"]
    dateUnlabel=sampleListDict["unlabelDate"]
    lastClosePriceUnlabel=XUnlabel[:,0,-1]



    #model test func
    def printResult(test_probas,yPred,yTarget,yReg):
        expectedEarningPerDayWithPred=np.mean(yReg[yPred])*100
        expectedEarningPerDayWithAllBuy=np.mean(yReg)*100
        print(f"probas_min:{np.min(test_probas):.4f} probas_max:{np.max(test_probas):.4f}")
        print(f"truePredCount:{yReg[yPred].shape[0]}")
        print(f"expectedEarningPerDayWithPred:{expectedEarningPerDayWithPred:.4f}%")
        print(f"expectedEarningPerDayWithAllBuy:{expectedEarningPerDayWithAllBuy:.4f}%")
        print(classification_report(yTarget, yPred))


    

    #date filter, only date after data
    testDateIndex=dateTest>=dateStart
    unlabelDateIndex=dateUnlabel>=dateStart
    XTest=XTest[testDateIndex]
    yTest=yTest[testDateIndex]
    tickerTest=tickerTest[testDateIndex]
    dateTest=dateTest[testDateIndex]
    lastClosePriceTest=lastClosePriceTest[testDateIndex]
    XUnlabel=XUnlabel[unlabelDateIndex]
    tickerUnlabel=tickerUnlabel[unlabelDateIndex]
    dateUnlabel=dateUnlabel[unlabelDateIndex]
    lastClosePriceUnlabel=lastClosePriceUnlabel[unlabelDateIndex]


    
    #load model
    PATH = Path(modelPath)
    learn = load_learner(PATH, cpu=True)


    #predict 
    test_probas_test, _, preds = learn.get_X_preds(XTest,bs=4)
    test_probas_unlabel, _, preds = learn.get_X_preds(XUnlabel,bs=4)

    test_probas_test=test_probas_test.numpy()[:,0]
    test_probas_unlabel=test_probas_unlabel.numpy()[:,0]
    closePredictTest=lastClosePriceTest*(1+test_probas_test)
    closePredictUnlabel=lastClosePriceUnlabel*(1+test_probas_unlabel)
    base=0.002
    printResult(test_probas_test,test_probas_test>base,yTest>base,yTest)

    
    
    #concat test and unlabel
    tickerConcat=np.concatenate((tickerTest,tickerUnlabel), axis = 0)
    dateConcat=np.concatenate((dateTest,dateUnlabel), axis = 0)
    closedPredictConcat=np.concatenate((closePredictTest,closePredictUnlabel ), axis = 0)
    return [     tickerConcat   ,dateConcat,    closedPredictConcat   ]
    
    
def getTimeAgo(day=0,month=0):
    timeAgo=datetime.now()- relativedelta(months=month,days=day)
    return timeAgo.strftime("%Y-%m-%d")
