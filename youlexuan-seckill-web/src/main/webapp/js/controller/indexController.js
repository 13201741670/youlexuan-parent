 //用户表控制层
app.controller('indexController' ,function($scope   ,indexService){


    //读取列表数据绑定到表单中  
	$scope.getname=function(){
		indexService.getname().success(
			function(response){
				$scope.list=response.getname;
			}
		);
	}

});