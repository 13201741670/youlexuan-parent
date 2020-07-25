 //控制层
 app.controller("goodsController", function ($scope,$controller,$http) {
     $controller('baseController',{$scope:$scope});//继承
      $scope.andNom=function(x){
      $scope.num=$scope.num+parseInt(x)
       if ($scope.num <1 )
       $scope.num = 1
      };
      $scope.specificationItems={};
      $scope.selectSpecification=function (name,value) {
       $scope.specificationItems[name] =value;
       $scope.searchSUK()
      }
      $scope.isSelected=function (name,value) {
      return ($scope.specificationItems[name] == value) ? "selected" :""
      };

        $scope.loadSku=function () {
         $scope.sku=SKUList[0]
         $scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
        }
          $scope.matchObject=function (map1,map2) {
           for (var k in map1){
            if (map1[k]!=map2[k]){
             return false;
            }
           }
           for (var k in map2){
            if (map1[k] != map2[k]){
             return false;
            }
           }
           return true;
          }
        $scope.searchSUK=function () {
         for (var i = 0 ;i<SKUList.length ;i++){
          if ($scope.matchObject(SKUList[i].spec,$scope.specificationItems)){
           $scope.sku=SKUList[i]
          }
         }
        }
       $scope.addCart=function () {
        alert("你购物的是:"+JSON.stringify($scope.sku)+",数量是:"+$scope.num)
       $http.get('http://localhost:9013/cart/addGoodsToCartList.do?itemId='
           + $scope.sku.id +'&num='+$scope.num,{'withCredentials':true}).success(function (resp) {
          if (resp.success){
            location.href='http://localhost:9013/cart.html';
          }else {
           alert(resp.message)
          }
       });
       }
 })