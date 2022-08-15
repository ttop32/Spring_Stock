
# library import-------------------------------------------
import os
os.environ["CUDA_VISIBLE_DEVICES"] = ""
from datetime import datetime, timedelta
import stock_util
import web_util






############init env var
baseDir="/app"
modelPath="/regression.pkl"



trainDateStart=stock_util.getTimeAgo(month=12)
trainDateStop=stock_util.getTimeAgo(month=12)
testDateStop='2070-01-01'
uploadDateStart=stock_util.getTimeAgo(day=5)


windowLength=50                  #input 50 days
predictDayAbove=1                #predict next 1 day above, pct change



################check time
print(datetime.now())
with open(baseDir+'/time.txt', 'w') as f:
    f.write('time:'+str(datetime.now()))



# ####################download data
pykrxPath, preprocess1Path, preprocess2Path=stock_util.creaetDir(baseDir, testDateStop,predictDayAbove)

stock_util.downloadCsvPykrxAll(trainDateStart,testDateStop,pykrxPath)
stock_util.preprocess1(trainDateStart,testDateStop,pykrxPath,preprocess1Path)
stock_util.preprocess2(trainDateStart,trainDateStop,testDateStop,predictDayAbove,windowLength,preprocess1Path,preprocess2Path)


# ########################uplolad
#base url
baseUrl="https://springstock.ddnsfree.com/api/"
web_util.changeBaseUrl(baseUrl)
#login
userId,userPw=web_util.getJsonIdPW(baseDir)
web_util.login(userId,userPw)
web_util.checkLoginStatus()
#upload
web_util.uploadOhlcv(preprocess1Path,uploadDateStart)
tickerTest,dateTest,closePredictTest  =stock_util.predict(preprocess2Path,modelPath,uploadDateStart)
web_util.uploadPredict(tickerTest, dateTest, closePredictTest)

