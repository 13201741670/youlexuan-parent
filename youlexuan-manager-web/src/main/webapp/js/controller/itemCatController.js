 //商品类目控制层
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){

	$controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}
		);
	}
 
	//分页
	$scope.findPage=function(page,rows){
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

	//查询实体
	$scope.findOne=function(id){
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;
			}
		);
	}

	//保存
	$scope.save=function(){
		var serviceObject;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改
		}else{
			serviceObject=itemCatService.add( $scope.entity,$scope.parentId);//增加
		}
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询
					$scope.findController($scope.parentId);
				}else{
					alert(response.message);
				}
			}
		);
	}


	//批量删除
	$scope.dele=function(){
		//获取选中的复选框
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					//$scope.reloadList();//刷新列表
					$scope.findController($scope.parentId);
					$scope.selectIds=[];
				}
			}
		);
	}

	$scope.searchEntity={};//定义搜索对象
	$scope.parentId=0;
	//搜索
	$scope.findController=function(parentId){
		$scope.parentId=parentId;
		itemCatService.findByParentId(parentId).success(
			function(response){
				$scope.list=response;
			}
		);
	};
	$scope.grade=1;
	$scope.setGrade=function (value) {
		$scope.grade=value;
	};
	$scope.selectList=function (p_entity) {
		if ($scope.grade == 1){
			$scope.entity_1=null;
			$scope.entity_2=null;
		}else if($scope.grade == 2){
			$scope.entity_1 =p_entity;
			$scope.entity_2 =null;
		}else {
			$scope.entity_2 =p_entity;
		}
		$scope.findController(p_entity.id);
	};
	$scope.findtypeTemplateList =function () {
		typeTemplateService.selectOptionList().success(function (resp) {
			$scope.typeTemplateList={data:resp};
		})
	}
})