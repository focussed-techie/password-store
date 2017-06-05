(function (){
    var changePasswordModule = angular.module("changePasswordModule");
    changePasswordModule.controller("changePasswordController",changePasswordController);

    function changePasswordController($scope,$http,$location, $routeParams,$window){
        var ctrl = this;
        ctrl.newPassword = '';
        ctrl.currentPassword = '';
        ctrl.message = '';



        $scope.signup = signup;



        function signup(){
            var userObject = {'currentPassword' : ctrl.currentPassword, 'newPassword' : ctrl.newPassword};

            $http.post('/changePassword',userObject)
                .then(function (response)
                {
                    ctrl.message = "Your password has been changed";
                    $window.location.href = "logout";


                },function(response){
                    ctrl.message = "There was some Changing your password. Sorry about that..."+response.data;
                });


        }

    }


})();