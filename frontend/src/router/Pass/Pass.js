import Pass from "../../components/Pass/Pass.vue"
import PassApply from "../../components/Pass/PassApply.vue"
import PassNow from "../../components/Pass/PassNow.vue"
import PassInfo from "@/components/Pass/PassInfo.vue"

//정기권 신청 관련
export default[
     { //정기권 발매 현황
        path: '/pass',
        name: 'pass',
        component: Pass
    },
     { //정기권신청
        path: '/passapply',
        name: 'passapply',
        component: PassApply
    },
       
       { //정기권 이용안내
        path: '/passinfo',
        name: 'passinfo',
        component: PassInfo
    },
    {   
        path: '/passnow',
        name: 'passnow',
        component: PassNow

    }



]
