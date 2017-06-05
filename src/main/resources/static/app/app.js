(function(){
  var passwordApp = angular.module("passwordApp",['ngRoute','addNewEntryModule','dashboardModule','changePasswordModule']);

  passwordApp.config(configuration);
    configuration.$inject = ['$locationProvider','$routeProvider'];

  function configuration($locationProvider, $routeProvider){

      $routeProvider.when('/dashboard',{
              controller : 'dashboardController' ,
              controllerAs :'dashboardCtrl',
              templateUrl : 'app/dashboard/dashboard-partial.html'
          })
          .when('/changePassword',{
              controller : 'changePasswordController' ,
              controllerAs :'passwordChangeCtrl',
              templateUrl : 'app/changePassword/change-password-partial.html'
          })
          /*.when('/addNewEntry',{
          controller : 'addNewEntryController' ,
          controllerAs :'addNewCtrl',
          templateUrl : 'app/addNewEntry/add-new-partial.html'
      })
*/
          .otherwise({
           redirectTo :'/dashboard'
       });

  }


})();