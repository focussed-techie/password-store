(function (){
    var alertModule = angular.module("alertModule");
    alertModule.service('alertService',alertService);



    function alertService($rootScope){
        $rootScope.alerts=[];
        $rootScope.alerts.push({type:'warning',message:'testing alert service'});

        this.addAlert = function addAlert(alert){
            $rootScope.alerts.push(alert);

        }
        this.getAlerts = function getAlerts(){
            return $rootScope.alerts;
        }
        this.removeAlert = function removeAlert(index){
            $rootScope.alerts.splice(index,1);
        }

    }


})();