//引入sql方法
const {
  exec
} = require('../db/mysql')

//查询所有通话记录
const call_logsList = (param) => {
  
  const isintercept = JSON.parse(param.isintercept)
  let sql = `SELECT * FROM call_logs WHERE host_number = ${param.host_number} AND isintercept = ${isintercept} `;
  // console.log(param.isintercept,sql);
  return exec(sql).then(rows => {
      return rows || {}
  })
}

// 添加通话记录
const addCall_logs = (body)=>{
  console.log(body);
  const sql0 = `SELECT name FROM CONTACT where host_number =${body.host_number} and contact_number= ${body.contact_number}`;
  let sql;
  console.log(sql0);
  exec(sql0).then(rows0=>{
    console.log(rows0);
    if(rows0.length===0){
      sql = `INSERT INTO call_logs(NAME,HOST_NUMBER,CONTACT_NUMBER,TIME,ISINTERCEPT) VALUES('${body.contact_number}','${body.host_number}','${body.contact_number}','${body.time}',${body.isintercept==="false"?0:1})`
    }
    else{
      sql =  `INSERT INTO call_logs(NAME,HOST_NUMBER,CONTACT_NUMBER,TIME,ISINTERCEPT) VALUES('${rows0[0].name}','${body.host_number}','${body.contact_number}','${body.time}',${body.isintercept==="false"?0:1})`
    }
    console.log(sql);
    return exec(sql).then(rows => {
      return "ok"
  })
  })

}
// 删除通话记录
const deleteCall_logs = (condition)=>{
  const sql = `DELETE FROM call_logs WHERE ${condition}`
  console.log(sql);
  return exec(sql).then(rows => {
      return rows || {}
  })
}

//暴露
module.exports = {
  call_logsList,
  addCall_logs,
  deleteCall_logs
}
