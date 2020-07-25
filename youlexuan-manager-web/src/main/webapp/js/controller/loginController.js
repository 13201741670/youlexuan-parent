app.controller("loginController",function ($scope,loginService) {
    $scope.showLoginName=function () {
      loginService.loginName().success(function (resp) {
          console.log(resp)
           $scope.loginName =resp.loginName;
      })
    }
})