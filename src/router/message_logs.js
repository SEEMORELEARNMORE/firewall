//引入express模块
const express = require('express')
// 创建 message_logs路由
const message_logs = express.Router()
//从../controller/message_logs 引入sql方法
const {
    message_logsList,
    addMessage_logs,
    deleteMessage_logs
} = require('../controller/message_logs')

//引入成功失败 返回方法
const {
    success,
    fail
} = require('../model/resModel')

//写接口  
message_logs.get('/message_logslist', async (req, res) => {
    //sql返回值
    const result = await message_logsList(req.query)
    //返回给前端
    res.send(result)
})

message_logs.post('/addmessage_logs', async (req, res) => {
    //sql返回值
    const result = await addMessage_logs(req.body)
    //返回给前端
    res.send(result)
})

message_logs.post('/deletemessage_logs', async (req, res) => {
    //sql返回值
    const result = await deleteMessage_logs(req.body.condition)
    //返回给前端
    res.send(success('返回的数据', result))
})

// 暴露
module.exports = message_logs
