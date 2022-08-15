# library import-------------------------------------------
import os
import time
from pathlib import Path
from urllib.parse import urljoin

import FinanceDataReader as fdr
import pandas as pd
import pandas_ta as ta
import parmap
import requests
import torch
from pykrx import stock
from tqdm import tqdm
import json


baseUrl=""
loginUrl=baseUrl+"member/login"
statusUrl=baseUrl+'member'
stockAddUrl=baseUrl+"stock/editor/"
stockListUrl=baseUrl+"stock/"
stockGraphAddUrl=baseUrl+"stockgraph/editor/"
StockGraphListUrl=baseUrl+"stockgraph/editor/"
stockGraphAddBatchUrl=baseUrl+"stockgraph/editor/batch"
session = requests.session()



def changeBaseUrl(newUrl):
    global baseUrl, loginUrl, statusUrl, stockAddUrl, stockListUrl, stockGraphAddUrl, StockGraphListUrl, stockGraphAddBatchUrl
    baseUrl=newUrl    
    loginUrl=baseUrl+"member/login"
    statusUrl=baseUrl+'member'
    stockAddUrl=baseUrl+"stock/editor/"
    stockListUrl=baseUrl+"stock/"
    stockGraphAddUrl=baseUrl+"stockgraph/editor/"
    StockGraphListUrl=baseUrl+"stockgraph/editor/"
    stockGraphAddBatchUrl=baseUrl+"stockgraph/editor/batch"
    print(baseUrl)

def getUrl():
    print(baseUrl)
    
def login(userId,userPw):
    res = session.post(loginUrl , json =  {
        "username": userId,
        "password": userPw
    })
    res.raise_for_status()
    print(res.content)

    
def checkLoginStatus():    
    res = session.get(statusUrl)
    print(res.content)
    
def updateStockDetail(tickerId,stockName):
    res = session.post(stockAddUrl ,json={
    "tickerId":tickerId,
    "stockName":stockName
    })
    res.raise_for_status()
    

def getStockDetailList():
    res = session.get(stockListUrl ,json={
    "size":100,
    })
    print(res.content)

    
def updateStockGraphData(tickerId,dateList,stockGraphMap):
    res = session.post(stockGraphAddUrl+tickerId ,json={
    "dateList":dateList,
    "stockGraphMap":stockGraphMap
    })
    res.raise_for_status()

def updateStockGraphBatchData(stockGraphRequestBatch):
    res = session.post(stockGraphAddBatchUrl ,json={
        "stockGraphRequestBatch":stockGraphRequestBatch        
    })
    res.raise_for_status()


    
def getStockGraphData(tickerId):
    res = session.get(StockGraphListUrl+tickerId)
    print(res.content)
    
    
    
    
    
    
def getJsonIdPW(baseDir="./"):
    userId=""
    userPw=""
    jsonFilePath=Path(baseDir)/"userIdPw.json"
    print(jsonFilePath)

    if jsonFilePath.exists():
        try:
            with open(jsonFilePath) as json_file:
                data = json.load(json_file)
                userId= data['userId']
                userPw= data['userPw']
        except Exception as e:
            print(e)
    return [userId,userPw]

def uploadOhlcv(preprocess1Path,uploadDateStart):

    for file in tqdm(list(preprocess1Path.glob('*.csv'))):
        code=Path(file).stem
        df = pd.read_csv(file,index_col=0)
        df.index=pd.to_datetime(df.index)
        df=df.loc[uploadDateStart:]
        if len(df)==0:continue


        dateList=df.index.strftime("%Y-%m-%d").tolist()
        stockGraphMap={
            "Close" : df["Close"].tolist(),
            "Open" : df["Open"].tolist(),
            "High" : df["High"].tolist(),
            "Low" : df["Low"].tolist(), 
        }

        uploadStockDetail(code)
        updateStockGraphData(code,dateList,stockGraphMap)
        
def uploadStockDetail(code):
    name=stock.get_market_ticker_name(code)
    updateStockDetail(code,name)

        
def uploadPredict(tickerTest, dateTest, closePredictTest):
    for ticker in tqdm(list(set(tickerTest))):
        testIndex=(tickerTest == ticker)

        dateList=dateTest[testIndex].tolist()
        stockGraphMap={
            "Predict" : closePredictTest[testIndex].tolist(),
        }

        uploadStockDetail(ticker)
        updateStockGraphData(ticker,dateList,stockGraphMap)

