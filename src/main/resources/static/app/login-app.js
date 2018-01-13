(function(){
    var passwordApp = angular.module("passwordLoginApp",['ui.router','loginModule','signUpModule','alertModule','rsaModule','ui.validate','ui.bootstrap']);

    passwordApp.config(configuration);
    configuration.$inject = ['$stateProvider','$urlRouterProvider'];

    function configuration($stateProvider,$urlRouterProvider){

      $urlRouterProvider.otherwise('/login');

        $stateProvider
            .state('base',{
                abstract:true,
                views :{
                    'headerView' :{
                        controller :'alertController',
                        controllerAs : 'alertCtrl',
                        templateUrl :'app/alert/alert-partial.html'
                    }
                }
            }).state('login',{
                url :'/login',
                parent : 'base',
                views : {
                    'bodyView@': {
                        controller:'loginController',
                        templateUrl :'app/login/login-partial.html'
                    },

            },resolve :{
                publicKey :  function (rsaService) {
                    rsaService.getKeyModulus();
                }
            }



             }).state('signup',{
                    url :'/signup',
                    parent : 'base',
                    views : {

               'bodyView@':{

                    controller :'signUpController as signUpctrl',
                    controllerAs :'signUpctrl',
                    templateUrl : 'app/signup/signup-partial.html'

                },


            }, resolve :{
                publicKey :  function (rsaService) {
                    rsaService.getKeyModulus();
                }
            }


            });

    }


})();