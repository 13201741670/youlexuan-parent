 //用户表控制层
app.controller('cartController' ,function($scope,$controller,cartService){

	$controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中  
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.list=response;
				$scope.totalValue=cartService.sum($scope.list);
			}
		);
	};
	$scope.addGoodsToCartList=function (itemId,num) {
		cartService.addGoodsToCartList(itemId,num).success(function (resp) {
			if (resp.success){
			$scope.findCartList()
			}else {
				alert(resp.message)
			}
		})
	};
	$scope.findListByUserId=function(){
		cartService.findListByUserId().success(
			function(response) {
				$scope.addressList = response;
				for (var i = 0; i <$scope.addressList.length ;i++){
					if ($scope.addressList[i].isDefault == '1'){
						$scope.address = $scope.addressList[i]
						break
					}
				}
			}
		);
	};
	$scope.selectAddress=function (address) {
		$scope.address=address
	};
	$scope.isSelectAddress=function (address) {
		return (address == $scope.address) ?"selected":"";
	};
	$scope.order={paymentType:1}
	$scope.setPayType=function (paymentType) {
		$scope.order.paymentType=paymentType;
	}
	$scope.isSelectPayType=function (paymentType) {
		return (paymentType ==$scope.order.paymentType) ? "selected":""

	}
	$scope.submitOrder=function () {
		$scope.order.receiverAreaName=$scope.address.address
		$scope.order.receiverMobile=$scope.address.mobile
		$scope.order.receiver=$scope.address.contact
		cartService.add($scope.order).success(function (resp) {
			if (resp.success){
				location.href="/pay.html"
			}
		})
	}
});