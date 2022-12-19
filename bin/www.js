//www.js
//引入express
const express = require('express')
// 实例化一个 express的对象
const app = express()
//监听3000端口
var server = app.listen(8088, function () {
    var host = server.address().address
    var port = server.address().port
    if (host == '::') {
        host = 'localhost:'
    }
    console.log("启动成功访问地址 http://", host, port)
})
//暴露
module.exports = app
