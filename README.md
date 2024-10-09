# yule_calc
javaFx做的一个收银业绩计算系统


# 启动

## 启动方式

- [加密启动](#加密启动)
  `JAVA_HOME -javaagent:yule_calc-1.0-SNAPSHOT-encrypted.jar -jar yule_calc-1.0-SNAPSHOT-encrypted.jar `
- [直接运行](#直接运行)
  `target\light\yule_calc目录下双击exe运行 [yule_calc-1.2-windows.zip](压缩包) `

### release

` -DdevelopmentVersion='1.0.3' -DreleaseVersion='1.0.2'`
` 省略后将按照现有版本号递增（releaseVersion表示要发布的版本号同时也会新增一个tag `
` developmentVersion表示接下来准备使用的版本号）`

`
mvn -B release:clean release:prepare release:perform -DdryRun=false -DdevelopmentVersion='1.0.3' -DreleaseVersion='1.0.2' -DignoreSnapshots=true
`

![运行图片_2xx](https://github.com/user-attachments/assets/b3963adc-f2eb-4074-ac85-8794bf65e16f)
