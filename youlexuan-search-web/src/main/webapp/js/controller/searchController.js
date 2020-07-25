app.controller('searchController',function($scope,searchService,$location){
    //搜索
    $scope.search=function(){
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
               $scope.buildPageLabel()
            }
        );
    };
    $scope.searchMap={"keywords":"","category":"","brand":"","spec":{},"price":"","pageNo":1,"pageSize":5,"sort":"","sortField":""};
    $scope.addSearchItem=function (key,value) {
        if (key =="category" || key=="brand" || key=="price"){
            $scope.searchMap[key] =value
        }else {
            $scope.searchMap.spec[key] =value
        }
        console.log($scope.searchMap)
        $scope.search()
    };
    $scope.removeSearchItem=function (key) {
        if (key =="category" || key=="brand" || key=="price"){
            $scope.searchMap[key] = ""
        }else {
           delete $scope.searchMap.spec[key]
        }
        $scope.search()
    }
    $scope.buildPageLabel=function () {
        $scope.pageLabel=[];
        var totalPages =$scope.resultMap.totalPages
        var firstPage=1
        var lastPage=totalPages
        if(totalPages >5){
            if ($scope.searchMap.pageNo < 3 ){
                lastPage = 5
            }else if ($scope.searchMap.pageNo > lastPage-2){
                firstPage =totalPages-4;
            }else {
                firstPage = $scope.searchMap.pageNo-2
                lastPage=$scope.searchMap.pageNo+2
            }
        }
        for (var i = firstPage; i<= lastPage;i++){
           $scope.pageLabel.push(i)
        }
        $scope.prePageNo=($scope.searchMap.pageNo > 1 )? ($scope.searchMap.pageNo-1):1
        $scope.postPageNo=($scope.searchMap.pageNo < $scope.resultMap.totalPages) ? ($scope.searchMap.pageNo+1):$scope.resultMap.totalPages
    }
    $scope.queryByPage=function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages){
        return
        }else {
            $scope.searchMap.pageNo=parseInt(pageNo)
            $scope.search()
        }
    };
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField =sortField;
        $scope.search()
    };
    $scope.keywordsIsBrand=function () {
       var brandList =  $scope.resultMap.brandList;
       var keywords = $scope.searchMap.keywords;
       for (var i=0 ; i <brandList.length;i++){
           if (keywords.indexOf(brandList[i].text) >= 0){
            return true
           }
       }
        return false
    };
    $scope.loadkeywords=function () {
        $scope.searchMap.keywords=$location.search()["keywords"]
        $scope.search()
    }
});