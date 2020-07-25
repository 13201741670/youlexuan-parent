var app = angular.module("youlexuan", []);
app.filter('trustHtml', ['$sce',function($sce){
    return function(html){
        return $sce.trustAsHtml(html);
    }
}])