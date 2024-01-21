import { get, post } from "./http";

// 左侧菜单选择的为全部时，根据路径，获取文件列表
export const getFileListByPath = (p) => get("/file/getfilelist", p);
// 左侧菜单选择的为除全部以外的类型时，根据类型，获取文件列表
export const getFileListByType = (p) => get("/file/selectfilebyfiletype", p);
// 创建文件夹或文件
export const createFile = (p) => post("/file/createfile", p);
// 获取存储空间已占用大小
export const getFileStorage = (p) => get("/filetransfer/getstorage", p);
export const getUserMaxStorage = (p) => get("/filetransfer/getuserstorage", p);
// 获取文件夹列表
export const getFileTree = (p) => get('/file/getfiletree', p);
// 单文件操作接口
// 文件删除
export const deleteFile = (p) => post('/file/deletefile', p);
// 文件移动
export const moveFile = (p) => post('/file/movefile', p);
// 文件重命名
export const renameFile = (p) => post('/file/renamefile', p);

// 批量文件操作接口
// 批量删除文件
export const batchDeleteFile = (p) => post("/file/batchdeletefile", p);
// 批量移动文件
export const batchMoveFile = (p) => post("/file/batchmovefile", p);