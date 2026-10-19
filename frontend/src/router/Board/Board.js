import BoardDetail from "../../components/Board/BoardDetail.vue";
import boardList from "../../components/Board/BoardList.vue";
import BoardWrite from "../../components/Board/BoardWrite.vue";

//자유게시판 관련 

export default[

    { //자유게시판
        path: '/boardlist',
        name: 'boardlist',
        component: boardList   
    },
    { //자유 WRUTE
        path: '/boardwrite',
        name: 'boardwrite',
        component: BoardWrite   
    },
    { //자유 DETAIL
        path: '/boarddetail',
        name: 'boarddetail',
        component: BoardDetail 
    },
]
