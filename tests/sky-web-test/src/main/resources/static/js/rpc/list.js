$(function () {
  //根据窗口调整表格高度
  $(window).resize(function () {
    $('#mytable').bootstrapTable('resetView', {
      height: tableHeight()
    })
  })
  //生成用户数据
  $('#mytable').bootstrapTable({
    method: 'get',
    contentType: "application/x-www-form-urlencoded",
    url: "testlist",
    columns: [{
      field: 'name',
      title: '名称'
    }, {
      field: 'type',
      title: '类型'
    }, {
      field: 'port',
      title: '端口号'
    }, {
      field: 'developer',
      title: '开发者'
    }, {
      field: 'mark',
      title: '备注'
    }, {
      field: 'nodes',
      title: '节点数'
    }],
    onDblClickRow: function (row) {
      window.location.href = 'rpc?serviceName=' + row.name;
    },
    locale: 'zh-CN',//中文支持,
  })
})

function tableHeight() {
  return $(window).height() - 140;
}
