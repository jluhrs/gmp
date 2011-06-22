#!/bin/bash 

DEFAULT_TIMEOUT=50000

function giapi-tester {
   java -jar giapi-tester-0.1.0-jar-with-dependencies.jar $@
   RETVAL=$?
   [ $RETVAL -eq 0 ] && echo "Command $2 OK"   
   [ $RETVAL -ne 0 ] && echo "Command $2 ERROR" && exit 1 
}

# Test initalzation and park
 giapi-tester -sc INIT -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc DATUM -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc PARK -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc INIT -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc END_GUIDE -activity PRESET_START -timeout $DEFAULT_TIMEOUT
# Test all possible apply commands
 
 giapi-tester -sc APPLY -config gpi:centerPinhole.mark=1 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:configPolarizer.deploy=1  -config gpi:configPolarizer.angle=5 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:configSteeringMirrors.selection=1 -config gpi:configSteeringMirrors.track=0 -config gpi:configSteeringMirrors.tip=0.0 -config gpi:configSteeringMirrors.tilt=0.0 -config gpi:configSteeringMirrors.focus=0.0 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:correct.selection=1 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config ao:dmShape.filename=XX -config ao:dmShape.dmFlag=1 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config ifs:log.temperatures=1 -config ifs:log.rate=5 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectLyotMask.maskStr=APLC_60 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config cal:acquireWhiteFringe.mark=1 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config cal:measureCalCentroids.filename=centroidFilename -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config ao:measureAOWFSCentriods.mark=1 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectAdc.deploy=1  -config gpi:selectAdc.overrideCas=-1 -config gpi:selectAdc.overrideZen=-1 -config gpi:selectAdc.orientation=0.1 -config gpi:selectAdc.power=0.2 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config ifs:selectIfsFilter.maskStr=Y -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectFocalPlaneMask.maskStr=FPM_Y -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectPupilPlaneMask.maskStr=APOD_Y -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectPupilCamera.deploy=0 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectPupilCamera.deploy=1 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectShutter.entranceShutter=0 -config gpi:selectShutter.calEntranceShutter=0 -config gpi:selectShutter.calExitShutter=0 -config gpi:selectShutter.calReferenceShutter=0 -config gpi:selectShutter.calScienceShutter=0  -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectSource.source=0 -config gpi:selectSource.intensity=60 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectSource.source=0 -config gpi:selectSource.intensity=0 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectSource.source=1 -config gpi:selectSource.intensity=60 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectSource.source=1 -config gpi:selectSource.intensity=0 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectSource.source=2 -config gpi:selectSource.intensity=60 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectSource.source=3 -config gpi:selectSource.intensity=60 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:selectSource.source=3 -config gpi:selectSource.intensity=0 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:takeDark.selection=0 -config gpi:takeDark.filename=ifsDark -config gpi:takeDark.intTime=10  -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:takeDark.selection=1 -config gpi:takeDark.filename=calDark -config gpi:takeDark.intTime=10  -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:takeDark.selection=2 -config gpi:takeDark.filename=aocDark -config gpi:takeDark.intTime=10  -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:takeFlat.selection=0 -config gpi:takeFlat.filename=ifsFlat -config gpi:takeFlat.intTime=1000  -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:takeFlat.selection=1 -config gpi:takeFlat.filename=calFlat -config gpi:takeFlat.intTime=1000  -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:takeFlat.selection=2 -config gpi:takeFlat.filename=aocFlat -config gpi:takeFlat.intTime=1000  -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config cal:transMap.filename=transMap -config cal:transMap.trackShutter=0 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:takeExposure.selection=0 -config gpi:takeExposure.filename=ifsExposure -config gpi:takeExposure.intTime=1000  -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:takeExposure.selection=1 -config gpi:takeExposure.filename=calExposure -config gpi:takeExposure.intTime=1000  -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:takeExposure.selection=2 -config gpi:takeExposure.filename=aocExposure -config gpi:takeExposure.intTime=1000  -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:configAo.useAo=1  -config gpi:configAo.magnitudeI=4 -config gpi:configAo.r0=22.2 -config gpi:configAo.optimize=1 -config gpi:configAo.useLastVals=0 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:configAoSpatialFilter.mode=1  -config gpi:configAoSpatialFilter.target=4.2 -config gpi:configAoSpatialfilter.now=0 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:configCal.useCal=0 -config gpi:configCal.magnitudeH=3 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:configFOVIfsOffset.xTarget=4 -config gpi:configFOVIfsOffset.yTarget=1 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:observationMode.mode=Y_coron -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config cal:measureHowfsOffsets.filename=howfsOffsets -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:calibPzt.coarsePzt=1 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
 giapi-tester -sc APPLY -config gpi:configIfs.integrationTime=1000 -config gpi:configIfs.readoutMode=4 -config gpi:configIfs.numReads=1 -config gpi:configIfs.numCoadds=1 -activity PRESET_START -timeout $DEFAULT_TIMEOUT
