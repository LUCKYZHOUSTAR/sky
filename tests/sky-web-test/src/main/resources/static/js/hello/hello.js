function jump(dataId, viewUrl) {
  // 审核
  var url = "";
  url = viewUrl + "?dataId=" + dataId + "&flag=2";
  window.location.href = url;

}