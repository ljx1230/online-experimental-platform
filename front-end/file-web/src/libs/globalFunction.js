export default function install(Vue) {
  //  加载缩略图
  Vue.prototype.downloadImgMin = function (row) {
    let fileUrl = row.fileUrl;
    if (fileUrl) {
      let index = fileUrl.lastIndexOf(".");
      fileUrl =
        "api" + fileUrl.substr(0, index) + "_min" + fileUrl.substr(index);
    }
    return fileUrl;
  };
}