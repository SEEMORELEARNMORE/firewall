//引入sql方法
const {
  exec
} = require('../db/mysql')

//查询手机权限
const phonePermission = (param) => {
  const sql0 = `SELECT * FROM blacklist where host_number = ${param.host_number} and intercept_number = ${param.intercept_number}`
  const sql1 = `select * from permission where host_number=${param.host_number}`
  const sql2 = `select * from contact where host_number=${param.host_number} and contact_number = ${param.intercept_number}`
  return exec(sql0).then(rows => {
    if(rows.length>0)
    return []
    else {
    return  exec(sql1).then(rows1=>{
        if(rows1[0].allphone===1)
        return []
        else if(rows1[0].strangephone===1){
          return exec(sql2).then(rows2=>{
            if(rows2.length>0){
              return [{isintercept:false}]
            }
            return []
          })
        }
        return [{isintercept:false}]
      })
    }
  })
}

//查询短信权限
const messagePermission = (param) => {
  const sql0 = `SELECT * FROM blacklist where host_number = ${param.host_number} and intercept_number = ${param.intercept_number}`
  const sql1 = `select * from message_permission where host_number=${param.host_number}`
  const sql2 = `select * from contact where host_number=${param.host_number} and contact_number = ${param.intercept_number}`
  return exec(sql0).then(rows => {
    console.log("messagepermission中的黑名单认证：");
     if(rows.length>0)
    return []
    else {
    return  exec(sql1).then(rows1=>{
        if(rows1[0].allmessage===1)
        return []
        else if(rows1[0].strangemessage===1){
          return exec(sql2).then(rows2=>{
            if(rows2.length>0){
              return [{isintercept:false}]
            }
            return []
          })
        }
        return [{isintercept:false}]
      })
    }
  })
}

// 修改权限
const updatePermission = (body)=>{
  const sql = `update permission set allphone=${parseInt(body.allphone)}, strangephone=${parseInt(body.strangephone)} where host_number=${body.host_number}`
  return exec(sql).then(rows => {
      return rows || {}
  })
}
// 修改权限
const updateMessagePermission = (body)=>{
  const sql = `update message_permission set allmessage=${parseInt(body.allmessage)}, strangemessage=${parseInt(body.strangemessage)} where host_number=${body.host_number}`
  return exec(sql).then(rows => {
      return rows || {}
  })
}

// 初始化通话权限
const initPhonePermission =(param)=>{
const sql = `select * from permission where host_number = ${param.host_number}`
return exec(sql).then(rows => {
  return rows || {}
})
}

// 初始化短信权限
const initMessagePermission =(param)=>{
  const sql = `select * from message_permission where host_number = ${param.host_number}`
  return exec(sql).then(rows => {
    return rows || {}
  })
  }

//暴露
module.exports = {
  phonePermission,
  updatePermission,
  initPhonePermission,
  initMessagePermission,
  messagePermission,
  updateMessagePermission
}
