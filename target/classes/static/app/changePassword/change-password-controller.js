(function (){
    var changePasswordModule = angular.module("changePasswordModule");
    changePasswordModule.controller("changePasswordController",changePasswordController);

    function changePasswordController($scope,$http,$window,alertService){
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
            var userObject = {'currentPassword' : ctrl.currentPassword, 'newPassword' : ctrl.newPassword};

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