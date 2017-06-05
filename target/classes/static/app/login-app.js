(function(){
    var passwordApp = angular.module("passwordLoginApp",['ngRoute','loginModule','signUpModule']);

    passwordApp.config(configuration);
    configuration.$inject = ['$locationProvider','$routeProvider'];

    function configuration($locationProvider, $routeProvider){

        $routeProvider.when('/login',{
            controller : 'loginController',
            templateUrl : 'app/login/login-partial.html'
        })
            .when('/signup',{
                controller :'signUpController as signUpctrl',
                controllerAs :'signUpctrl',
                templateUrl : 'app/signup/signup-partial.html'

            })
            .otherwise({
                redirectTo :'/login'
            });

    }


})();