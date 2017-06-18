(function(){
  var passwordApp = angular.module("passwordApp",['addNewEntryModule','dashboardModule','changePasswordModule','ui.validate','ui.router']);

  passwordApp.config(configuration);
    configuration.$inject = ['$stateProvider','$urlRouterProvider'];

  function configuration($stateProvider,$urlRouterProvider){

      $urlRouterProvider.otherwise('/dashboard');

      $stateProvider
          .state('dashboard',{
          url:"/dashboard",
          controller:'dashboardController',
          controllerAs :'dashboardCtrl',
          templateUrl :'app/dashboard/dashboard-partial.html'

      }).state('changePassword',{
          url: '/changePassword',
          controller :'changePasswordController as passwordChangeCtrl',
          controllerAs :'passwordChangeCtrl',
          templateUrl : 'app/changePassword/change-password-partial.html'

      });


  }


})();