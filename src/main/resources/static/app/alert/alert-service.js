(function (){
    var alertModule = angular.module("alertModule");
    alertModule.service('alertService',alertService);



    function alertService($rootScope){
        var service =this;

        service.alerts=[];
       // service.alerts.push({type : 'warning',message : 'testing alert service'});

        this.addAlert = function addAlert(alert){
            service.alerts.push(alert);

        }
        this.getAlerts = function getAlerts(){
            return service.alerts;
        }
        this.removeAlert = function removeAlert(index){
            service.alerts.splice(index,1);
        }

    }


})();