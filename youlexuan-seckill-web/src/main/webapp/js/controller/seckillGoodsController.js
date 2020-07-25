 //控制层
app.controller('seckillGoodsController' ,function($scope,$controller  ,seckillGoodsService,$location,$interval,seckillOrderService){

	$controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		seckillGoodsService.findAll().success(
			function(response){
				$scope.list=response;
			}
		);
	}
 
	//分页
	$scope.findPage=function(page,rows){
		seckillGoodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	};
	$scope.getShow=function(id){
		location.href="seckill-item.html#?id="+id
	}

	//查询实体
	$scope.findOne=function(){

		seckillGoodsService.findOne($location.search()['id']).success(
			function(response){
				$scope.pojo= response;
				allsecond=Math.floor(((new Date($scope.pojo.endTime).getTime())-new Date().getTime())/1000)
				time = $interval(function () {
					if (allsecond > 0){
						allsecond--;
						$scope.tiemString = convertTimeString(allsecond)
					}else {
						$interval.cancel(time)
						location.href="seckill-index.html"
					}
				},1000)
			}
		);
	}
	$scope.show=function(entity){
		return new Date(entity.endTime).getTime() > new Date().getTime()
	};
	$scope.submitOrder=function(){
		seckillOrderService.submitOrder($scope.pojo.id).success(function (resp) {
			if (resp.success){
			location.href='pay.html'
			}
		})
	}

	//保存
	$scope.save=function(){
		var serviceObject;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			serviceObject=seckillGoodsService.update( $scope.entity ); //修改
		}else{
			serviceObject=seckillGoodsService.add( $scope.entity  );//增加
		}
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}
		);
	}


	//批量删除
	$scope.dele=function(){
		//获取选中的复选框
		seckillGoodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}
			}
		);
	}

	$scope.searchEntity={};//定义搜索对象

	//搜索
	$scope.search=function(page,rows){
		seckillGoodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

});
 convertTimeString = function(allsecond){
	 var days= Math.floor(allsecond/(60*60*24));//天数
	 var hours= Math.floor((allsecond-days*60*60*24)/(60*60));//小时数
	 var minutes= Math.floor((allsecond -days*60*60*24 - hours*60*60)/60);//分钟数
	 var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
	 var timeString="";
	 if(days>0){
		 timeString=days+"天 ";
	 }
	 return timeString+hours+":"+minutes+":"+seconds;
 }