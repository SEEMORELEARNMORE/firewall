//引入express模块
const express = require('express')
// 创建 blacklist路由
const blacklist = express.Router()
//从../controller/blacklist 引入sql方法
const {
    blacklistList,
    addBlacklist,
    isBlacklist,
    deleteBlacklist
} = require('../controller/blacklist')

//引入成功失败 返回方法
const {
    success,
    fail
} = require('../model/resModel')

//写接口  
blacklist.get('/blacklistlist', async (req, res) => {
    //sql返回值
    const result = await blacklistList(req.query)
    //返回给前端
    res.send(result)
})

blacklist.get('/isblacklist', async (req, res) => {
    //sql返回值
    const result = await isBlacklist(req.query)
    //返回给前端
    res.send({result:result.length!==0?true:false})
})

blacklist.post('/addblacklist', async (req, res) => {
    //sql返回值
    console.log("req.body:",req.body);
    const result = await addBlacklist(req.body.data)
    //返回给前端
    res.send( result)
})

blacklist.get('/deleteblacklist', async (req, res) => {
    //sql返回值
    const result = await deleteBlacklist(req.query)
    //返回给前端
    console.log(result);
    res.send(result)
})

// 暴露
module.exports = blacklist
