(function (){
    var changePasswordModule = angular.module("changePasswordModule");
    changePasswordModule.controller("changePasswordController",changePasswordController);

    function changePasswordController($scope,$http,$window,alertService,rsaService){
        var ctrl = this;
        ctrl.newPassword = '';
        ctrl.newConfirmPassword = '';
        ctrl.currentPassword = '';
        ctrl.message = '';




        $scope.signup = signup;
        $scope.match=match;

        function match(value){
            return value ==ctrl.newPassword;
        }


        function signup(){

            var encryptedPassword = rsaService.encryptWithPublicKeyData(ctrl.newPassword);
            var encryptedOldPassword = rsaService.encryptWithPublicKeyData(ctrl.currentPassword);
            var userObject = {'currentPassword' : encryptedOldPassword, 'newPassword' : encryptedPassword};

            $http.post('/changePassword',userObject)
                .then(function (response)
                {
                    alertService.addAlert({type:'warning', message : 'Your Password is changed!!!'});
                    $window.location.href = "logout";


                },function(response){
                    alertService.addAlert({type:'danger', message : 'There was an error changing your password. Please try again later!!!'});

                });


        }

    }


})();