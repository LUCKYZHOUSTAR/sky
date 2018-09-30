$(function () {

  window.operationEvents = {
    "click #onlineEditor": function (e, value, row, index) {
      var serviceName = $('#servername').val();
      alert(row.address);
      alert(serviceName);

      $.ajax({
        type: "get",
        dataType: "json",
        url: "offline",
        success: function (data) {
          alert(data.success);
          $("#mytable").bootstrapTable('refresh', refresh("testservices"));
          $("#onlinetable").bootstrapTable('refresh', refresh("testservices"));


        },
        error: function (error) {
          alert("下线失败，出现异常的信息");
          console.log(error);
        }
      });
    },
    "click #offline": function (e, value, row, index) {
      var serviceName = $('#servername').val();
      alert(row.address);
      alert(serviceName);

      $.ajax({
        type: "get",
        dataType: "json",
        url: "offline",
        success: function (data) {
          alert(data.success);

        },
        error: function (error) {
          alert("下线失败，出现异常的信息");
          console.log(error);
        }
      });
    },
    "click #status": function (e, value, row, index) {
      var serviceName = $('#servername').val();
      alert(row.address);
      alert(serviceName);

      $.ajax({
        type: "get",
        dataType: "json",
        url: "offline",
        success: function (data) {
          alert(data.success);

        },
        error: function (error) {
          alert("下线失败，出现异常的信息");
          console.log(error);
        }
      });
    }
  }
  $('#mytable').bootstrapTable({
    method: 'get',
    queryParams: queryParams,
    url: "testservices",
    columns: [{
      field: 'address',
      title: '地址'
    }, {
      field: 'type',
      title: '类型'
    }, {
      field: 'version',
      title: '版本号'
    }, {
      field: 'clients',
      title: '连接数'
    }, {
      field: 'note',
      title: '备注'
    }, {
      field: 'operation',
      title: '操作',
      events: operationEvents,
      formatter: addOfflineButions,
    }],
    locale: 'zh-CN',//中文支持,
  })

  $('#onlinetable').bootstrapTable({
    method: 'get',
    queryParams: queryParams,
    url: "onlinetestservices",
    columns: [{
      field: 'address',
      title: '地址'
    }, {
      field: 'type',
      title: '类型'
    }, {
      field: 'version',
      title: '版本号'
    }, {
      field: 'clients',
      title: '连接数'
    }, {
      field: 'note',
      title: '备注'
    }, {
      field: 'operation',
      title: '操作',
      events: operationEvents,
      formatter: addButions,
    }],
    locale: 'zh-CN',//中文支持,
  })

  function queryParams(params) {
    var temp = {
      serviceName: $('#servername').val()
    }
    console.log(temp)
    return temp;
  }

  function addOfflineButions() {
    return ['<button id="onlineEditor"  type="button" style="background-color: #7ED321;width: 76px;height: 36px;color: #FFFFFF"  class="btn btn-default">下线</button>',
      '<button id="status"  type="button" style="background-color: #7ED321;width: 76px;height: 36px;color: #FFFFFF"  class="btn btn-default">服务</button>'].join(
        "");
  }

  function addButions() {
    return ['<button id="offline"  type="button" style="background-color: #7ED321;width: 76px;height: 36px;color: #FFFFFF"  class="btn btn-default">上线</button>'].join(
        "");
  }

  function refresh(address) {
    var ad = {
      url: "/service/" + address
    }

    return ad;
  }
})
//生成用户数据


