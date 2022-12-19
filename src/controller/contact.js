//引入sql方法
const {
    exec
} = require('../db/mysql')

//查询所有联系人
const contactList = (param) => {
    const sql = `SELECT * FROM CONTACT where host_number =${param.host_number}`
    console.log(sql);
    return exec(sql).then(rows => {
        return rows || {}
    })
}

// 添加联系人
const addContact = (value)=>{
    const sql = `INSERT INTO CONTACT(NAME,HOST_NUMBER,CONTACT_NUMBER) VALUES (${value})`
    console.log(sql);
    return exec(sql).then(rows => {
        return rows || {}
    })
}
// 修改联系人
const updateContact = (value,condition)=>{
    const sql = `UPDATE CONTACT SET ${value} WHERE ${condition} `
    console.log(sql);
    return exec(sql).then(rows => {
        return rows || {}
    })
}
// 删除联系人
const deleteContact = (condition)=>{
    const sql = `DELETE FROM CONTACT WHERE ${condition}`
    console.log(sql);
    return exec(sql).then(rows => {
        return rows || {}
    })
}
//暴露
module.exports = {
    contactList,
    addContact,
    updateContact,
    deleteContact
}
