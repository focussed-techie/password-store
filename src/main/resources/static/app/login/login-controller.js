(function (){
    var loginModule = angular.module("loginModule");
    loginModule.controller("loginController",loginController);

    function loginController($scope){
        this.username = '';
        this.password = '';

    }


})();