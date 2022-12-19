//引入sql方法
const {
  exec
} = require('../db/mysql')
const {
  messagePermission
} = require('./permission')
//查询所有通话记录
const message_logsList = (param) => {
  const isintercept = JSON.parse(param.isintercept)
  let sql  = `SELECT * FROM message_logs where host_number =${param.host_number} and isintercept = ${isintercept}`
  // console.log(sql);
  return exec(sql).then(rows => {
      return rows || {}
  })
}

// 添加通话记录
const addMessage_logs = (body)=>{
  console.log(body);
  const sql0 = `SELECT * FROM blacklist where host_number = ${body.host_number} and intercept_number = ${body.contact_number}`
  const sql1 = `select * from message_permission where host_number=${body.host_number}`
  const sql2 = `select * from contact where host_number=${body.host_number} and contact_number = ${body.contact_number}`
  let isintercept =0;
  let name = body.contact_number;
  let  sql;
  return exec(sql2).then(rows2 => {
    //  查找是否在联系人中
     if(rows2.length>0)
     {name = rows2[0].name}
    exec(sql0).then(rows0=>{
      // 查询是否在黑名单中
      if(rows0.length>0){
        isintercept = 1
         sql =  `INSERT INTO message_logs(NAME,HOST_NUMBER,CONTACT_NUMBER,MESSAGE_CONTENT,TIME,ISINTERCEPT) VALUES('${name}','${body.host_number}','${body.contact_number}','${body.message_content}','${body.time}',${isintercept})`
        return exec(sql).then(rows=>{ rows|| {}})
      }
      else{
        return exec(sql1).then(rows1 =>{
          //  全部拦截
         if(rows1[0].allmessage===1)
           {
            isintercept = 1;
            sql =  `INSERT INTO message_logs(NAME,HOST_NUMBER,CONTACT_NUMBER,MESSAGE_CONTENT,TIME,ISINTERCEPT) VALUES('${name}','${body.host_number}','${body.contact_number}','${body.message_content}','${body.time}',${isintercept})`
            return exec(sql).then(rows=>{ rows|| {}})
          }
          // 拦截陌生
         else if(rows1[0].strangemessage=== 1){
           if(rows2.length===0){
            isintercept = 1;
           }
            sql =  `INSERT INTO message_logs(NAME,HOST_NUMBER,CONTACT_NUMBER,MESSAGE_CONTENT,TIME,ISINTERCEPT) VALUES('${name}','${body.host_number}','${body.contact_number}','${body.message_content}','${body.time}',${isintercept})`
            return exec(sql).then(rows=>{ rows|| {}})
        }
        // 非黑名单且没有特殊要求不需要拦截
        else {
          sql =  `INSERT INTO message_logs(NAME,HOST_NUMBER,CONTACT_NUMBER,MESSAGE_CONTENT,TIME,ISINTERCEPT) VALUES('${name}','${body.host_number}','${body.contact_number}','${body.message_content}','${body.time}',${isintercept})`
          return exec(sql).then(rows=>{ rows|| {}})
        }
        })
      }
        
      })
  })
}
// 删除通话记录
const deleteMessage_logs = (condition)=>{
  const sql = `DELETE FROM message_logs WHERE ${condition}`
  console.log(sql);
  return exec(sql).then(rows => {
      return rows || {}
  })
}

//暴露
module.exports = {
  message_logsList,
  addMessage_logs,
  deleteMessage_logs
}
