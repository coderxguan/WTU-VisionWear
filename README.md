# 后端项目
jdk 17

test


# 前端


## 项目安装
```shell
npm install
npm run dev
```


# 项目打包
## 打包后端
```shell
cd backend
mvn clean install
cd target
scp backend-0.0.1-SNAPSHOT.jar root@8.155.5.178:/opt/vision-wear
```

## 打包前端
```shell
cd frontend
npm run build
scp -r dist root@8.155.5.178:/opt/vision-wear/
```

