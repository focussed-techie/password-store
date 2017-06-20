(function (){
    var alertModule = angular.module("alertModule");
    alertModule.controller("alertController",alertController);

    function alertController($scope,alertService){

        var ctrl = this;
        $scope.closeAlert = closeAlert;



        function closeAlert(index){
            alertService.removeAlert(index);
        }

    }


})();