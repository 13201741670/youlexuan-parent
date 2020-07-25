 //用户表控制层
app.controller('payController' ,function($scope,$controller,payService,$location){

	$controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中  
	$scope.createNative=function(){
		payService.createNative().success(
			function(response){
				$scope.money=response.total_amount;
				$scope.out_trade_no=response.out_trade_no
				var qr = new QRious({
					element: document.getElementById('qrcodes'),
					size: 250,
					level: 'H',
					value: response.qrcode });
				$scope.queryPayStatus($scope.out_trade_no)
			}
		);
	};
	$scope.queryPayStatus=function (out_trade_no) {
		payService.queryPayStatus(out_trade_no).success(function (resp) {
			if (resp.success){
				location.href="paysuccess.html#?money="+$scope.money
			}else {
				if (resp.message == "二维码超时"){
				document.getElementById("timeout").innerHTML ='二维码已过 期，刷新页面重新获取二维码.';
				}else {
					location.href="payfail.html";
				}
			}
		})
	};
	$scope.getMoney=function () {
		return $location.search()['money'];
	}
});