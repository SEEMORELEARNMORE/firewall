//引入express模块
const express = require('express')
// 创建 permission路由
const permission = express.Router()
//从../controller/permission 引入sql方法
const {
   phonePermission,
   updatePermission,
   initPhonePermission,
   initMessagePermission,
   messagePermission,
   updateMessagePermission
} = require('../controller/permission')

//写接口  
permission.get('/phonepermission', async (req, res) => {
    //sql返回值
    const result = await phonePermission(req.query)
    //返回给前端 为空——true拦截
    res.send({result:result.length?false:true})
})

permission.get('/messagepermission', async (req, res) => {
  //sql返回值
  const result = await messagePermission(req.query)
  //返回给前端 为空——true拦截
  res.send({result:result.length?false:true})
})

permission.get("/initphonepermission",async(req,res)=>{
  const result = await initPhonePermission(req.query)
  res.send(result)
})

permission.get("/initmessagepermission",async(req,res)=>{
  const result = await initMessagePermission(req.query)
  console.log("initmessagepermission的result：",result);
  res.send(result)
})

permission.post('/updatepermission', async (req, res) => {
    //sql返回值
    console.log("req.body:",req.body);
    const result = await updatePermission(req.body)
    //返回给前端
    res.send( result)
})

permission.post('/updatemessagepermission', async (req, res) => {
  //sql返回值
  console.log("req.body:",req.body);
  const result = await updateMessagePermission(req.body)
  //返回给前端
  res.send( result)
})


// 暴露
module.exports = permission
