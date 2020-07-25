//用户表服务层
app.service('cartService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findCartList=function(){
		return $http.get('../cart/findCartList.do');
	};
	this.addGoodsToCartList=function (itemId,num) {
		return $http.post("../cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num)
	}
	this.findListByUserId=function(){
		return $http.get('../address/findListByUserId.do');
	};
	this.sum=function (list) {
		var totalValue={totalNum:0,totalMoney:0.00};
		for (var i = 0;i<list.length;i++){
			var cart = list[i]
			for(var j = 0 ; j <cart.tbOrderItems.length ;j++){
				var orderItems = cart.tbOrderItems[j]
				totalValue.totalNum+=orderItems.num
				totalValue.totalMoney+=orderItems.totalFee
			}
		}
		return totalValue;
	};
	this.add=function (order) {
		return $http.post("../order/add.do",order);
	}
});