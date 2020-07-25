 //控制层
app.controller('goodsController' ,function($scope,$controller ,$location,goodsService,uploadService,itemCatService,typeTemplateService){

	$controller('baseController',{$scope:$scope});//继承
	$scope.entity = {tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]}}

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}
		);
	}
 
	//分页
	$scope.findPage=function(page,rows){
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

	//查询实体
	$scope.findOne=function(){
		var id = $location.search()['id']
		if(id == null){
			return
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				editor.html($scope.entity.tbGoodsDesc.introduction);
				$scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages)
				$scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems)
				$scope.entity.tbGoodsDesc.specificationItems=JSON.parse($scope.entity.tbGoodsDesc.specificationItems)
				for (var i = 0; i < $scope.entity.tbItems.length; i++){
					$scope.entity.tbItems[i].spec = JSON.parse($scope.entity.tbItems[i].spec)
				}
			}
		);
	}

	//保存
	$scope.save=function(){
		$scope.entity.tbGoodsDesc.introduction = editor.html();
		var serviceObject;//服务层对象
		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加
		}
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询
		        	alert(response.message)
					$scope.entity = {tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]}}
					editor.html('');
					location.href="goods.html";
				}else{
					alert(response.message);
				}
			}
		);
	}


	//批量删除
	$scope.dele=function(){
		//获取选中的复选框
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	};
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(function (resp) {
			if (resp.success){
				$scope.image_entity.url =resp.message;
			}else {
				alert(resp.message)
			}

		})
	};
	//添加图片
	$scope.add_image_entity =function () {
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity)
	};
	$scope.del_image_entity=function (index) {
		$scope.entity.tbGoodsDesc.itemImages.splice(index,1)
	}
	//三级目录
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(function (resp) {
			$scope.itemCat1List=resp;
		})
	};
	$scope.$watch("entity.tbGoods.category1Id",function (newvalue,oldvalue) {
		if (newvalue){
		itemCatService.findByParentId(newvalue).success(function (resp) {
			$scope.itemCat2List=resp
		})
		}
	})
	$scope.$watch("entity.tbGoods.category2Id",function (newvalue,oldvalue) {
		if (newvalue) {
			itemCatService.findByParentId(newvalue).success(function (resp) {
				$scope.itemCat3List = resp

			})
		}
	});
	$scope.$watch("entity.tbGoods.category3Id",function (newvalue,oldvalue) {
		if (newvalue) {
			itemCatService.findOne(newvalue).success(function (resp) {
				$scope.entity.tbGoods.typeTemplateId = resp.typeId

			})
		}
	});
	$scope.$watch("entity.tbGoods.typeTemplateId",function (newvalue,oldvalue) {
		if (newvalue) {
			typeTemplateService.findOne(newvalue).success(function (resp) {
				$scope.typeTemplateId =resp;
				$scope.typeTemplateId.brandIds = JSON.parse($scope.typeTemplateId.brandIds)
				if($location.search()['id']==null) {
					$scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplateId.customAttributeItems)
				}
			});
			typeTemplateService.selectSpliList(newvalue).success(function (resp) {
				$scope.SpecliList =resp;
			})
		}
	});
	$scope.updateSpecAttribute=function ($event,name,value) {
	var object=	$scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems,'attributeName', name)
		if(object != null){
			if ($event.target.checked){
				object.attributeValue.push(value)
			}else {
				object.attributeValue.splice(object.attributeValue.indexOf(value,1))
			}if (object.attributeValue.length == 0){
			var tar=	$scope.entity.tbGoodsDesc.specificationItems.indexOf(object,1)
				$scope.entity.tbGoodsDesc.specificationItems.splice(tar,1)
			}
		}else {
			$scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}
	};
	$scope.createItemList=function () {
		$scope.entity.tbItems=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];
		var items = $scope.entity.tbGoodsDesc.specificationItems;
		for (var i = 0 ; i < items.length ;i++){
			$scope.entity.tbItems=addColumn($scope.entity.tbItems,items[i].attributeName,items[i].attributeValue)
		}
	};
	/*勾选是的回显*/
	$scope.checkAttributeValue=function (name,value) {
		var itme = $scope.entity.tbGoodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(itme,"attributeName",name)
		if(object == null){
			return false
		}else {
			if (object.attributeValue.indexOf(value) >= 0){
				return  true
			}else {
				return false
			}
		}
	}
	$scope.reloadList = function () {
		$scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
	};
	$scope.status=['未审核','审核通过','审核未通过','关闭'];
	$scope.ItemCatList=[]
	$scope.findItemCatList=function () {
		itemCatService.findAll().success(function (resp) {
			for (var i =0 ; i < resp.length ; i++){
				$scope.ItemCatList[resp[i].id] =resp[i].name
			}

		})
	};
	$scope.addGoods=function () {
		window.location.href="/admin/goods_edit.html"
	};

});
	 addColumn=function (list,key,values) {
			var newList=[];
			for (var i = 0;i<list.length;i++){
				var oldRow=list[i]
				for(var j = 0;j<values.length;j++){
					var newRow = JSON.parse(JSON.stringify(oldRow))
					newRow.spec[key]=values[j]
					newList.push(newRow)
				}
			}
			return newList;
	 };

