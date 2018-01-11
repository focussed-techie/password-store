(function(){
  var passwordApp = angular.module("passwordApp",['addNewEntryModule','dashboardModule','changePasswordModule','alertModule','rsaModule','ui.validate','ui.router','ui.bootstrap']);

  passwordApp.config(configuration);
    configuration.$inject = ['$stateProvider','$urlRouterProvider'];

  function configuration($stateProvider,$urlRouterProvider){

      $urlRouterProvider.otherwise('/dashboard');

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
          })
          .state('dashboard',{
            url:"/dashboard",
              parent : 'base',
          views : {
              'bodyView@': {
                  controller: 'dashboardController',
                  controllerAs: 'dashboardCtrl',
                  templateUrl: 'app/dashboard/dashboard-partial.html'
              },
              resolve :{
                  data : function (rsaService,$q) {
                      var promise =  $q.defer();
                      rsaService.populateKeys().then(promise.resolve(''));
                      return promise.promise;

                  }

              }
          },


      }).state('changePassword',{
          url: '/changePassword',
          parent : 'base',
          views : {
              'bodyView@': {
                  controller :'changePasswordController as passwordChangeCtrl',
                  controllerAs :'passwordChangeCtrl',
                  templateUrl : 'app/changePassword/change-password-partial.html'

              }
          }

      });


  }


})();