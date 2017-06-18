(function(){
    var passwordApp = angular.module("passwordLoginApp",['ui.router','loginModule','signUpModule','ui.validate']);

    passwordApp.config(configuration);
    configuration.$inject = ['$stateProvider','$urlRouterProvider'];

    function configuration($stateProvider,$urlRouterProvider){

      $urlRouterProvider.otherwise('/login');

        $stateProvider
            .state('login',{
            url:"/login",
            controller:'loginController',
            templateUrl :'app/login/login-partial.html'

        }).state('signup',{
            url: '/signup',
            controller :'signUpController as signUpctrl',
            controllerAs :'signUpctrl',
            templateUrl : 'app/signup/signup-partial.html'

        });

    }


})();