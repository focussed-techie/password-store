(function (){
    var rsaModule = angular.module("rsaModule");
    rsaModule.service('rsaService',rsaService);



    function rsaService($rootScope,$http){
        var service =this;

        service.publicKey=getKeyModulus();
        service.rsa=new RSAKey();
       // getKeyModulus();
        // service.alerts.push({type : 'warning',message : 'testing alert service'});

        this.encryptData = function encryptData(data){
            return service.rsa.encrypt(data);

        }
        this.decryptData = function decryptData(data){
            return service.rsa.decrypt(data);
        }

        function getKeyModulus(){
            $http.get("/publicKey").then(function(response){
                service.publicKey=response.data;
                service.rsa.setPublic(service.publicKey, '10001');
            });
        }

    }


})();