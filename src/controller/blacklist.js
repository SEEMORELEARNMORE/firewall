//引入sql方法
const {
  exec
} = require('../db/mysql')

//查询所有黑名单号码
const blacklistList = (param) => {
  const sql = `SELECT * FROM blacklist where host_number = ${param.host_number}`
  return exec(sql).then(rows => {
      return rows || {}
  })
}

// 添加到黑名单
const addBlacklist = (value)=>{
  let arr = value.split(",")
  const sql = `INSERT INTO blacklist(HOST_NUMBER,INTERCEPT_NUMBER) VALUES (${arr[0]},${arr[1]})`
  console.log(value,sql);
  return exec(sql).then(rows => {
      return rows || {}
  })
}

//判断是否为黑名单号码
const isBlacklist = (param) => {
  const sql = `SELECT * FROM blacklist where host_number = ${param.host_number} and intercept_number = ${param.intercept_number}`
  console.log(sql);
  return exec(sql).then(rows => {
      return rows || []
  })
}

// 删除黑名单号码
const deleteBlacklist = (param)=>{
  const sql = `DELETE FROM blacklist WHERE host_number= ${param.host_number} and intercept_number = ${param.intercept_number}`
  console.log(sql);
  return exec(sql).then(rows => {
      return rows || {}
  })
}
//暴露
module.exports = {
  blacklistList,
  addBlacklist,
  isBlacklist,
  deleteBlacklist
}
