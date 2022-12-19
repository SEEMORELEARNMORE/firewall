//引入express模块
const express = require('express')
// 创建 contact路由
const contact = express.Router()
//从../controller/contact 引入sql方法
const {
    contactList,
    addContact,
    updateContact,
    deleteContact
} = require('../controller/contact')

//引入成功失败 返回方法
const {
    success,
    fail
} = require('../model/resModel')

//写接口  
contact.get('/contactlist', async (req, res) => {
    //sql返回值
    const result = await contactList(req.query)
    //返回给前端
    console.log(result);

    res.send(result)
    
})

contact.post('/addcontact', async (req, res) => {
    //sql返回值
    const result = await addContact(req.body.value)
    //返回给前端
    res.send(success('返回的数据', result))
})

contact.post('/updatecontact', async (req, res) => {
    //sql返回值
    const result = await updateContact(req.body.value,req.body.condition)
    //返回给前端
    res.send(success('返回的数据', result))
})

contact.post('/deletecontact', async (req, res) => {
    //sql返回值
    const result = await deleteContact(req.body.condition)
    //返回给前端
    res.send(success('返回的数据', result))
})

// 暴露
module.exports = contact
