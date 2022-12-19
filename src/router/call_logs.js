//引入express模块
const express = require('express')
// 创建 call_logs路由
const call_logs = express.Router()
//从../controller/call_logs 引入sql方法
const {
    call_logsList,
    addCall_logs,
    deleteCall_logs
} = require('../controller/call_logs')

//引入成功失败 返回方法
const {
    success,
    fail
} = require('../model/resModel')

//写接口  
call_logs.get('/call_logslist', async (req, res) => {
    //sql返回值
    const result = await call_logsList(req.query)
    //返回给前端
    console.log(result);
    res.send(result)
})

call_logs.post('/addcall_logs', async (req, res) => {
    //sql返回值
    const result = await addCall_logs(req.body)
    //返回给前端
    res.send(result)
})

call_logs.post('/deletecall_logs', async (req, res) => {
    //sql返回值
    const result = await deleteCall_logs(req.body.condition)
    //返回给前端
    res.send(success('返回的数据', result))
})

// 暴露
module.exports = call_logs
