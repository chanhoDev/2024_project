package com.chanho.compose

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.chanho.compose.TodoDestinationArgs.TASK_ID_ARG
import com.chanho.compose.TodoDestinationArgs.TITLE_ARG
import com.chanho.compose.TodoDestinationArgs.USER_MESSAGE_ARG
import com.chanho.compose.TodoScreens.ADD_EDIT_TASK_SCREEN
import com.chanho.compose.TodoScreens.STATISTICS_SCREEN
import com.chanho.compose.TodoScreens.TASKS_SCREEN
import com.chanho.compose.TodoScreens.TASK_DETAIL_SCREEN


private object TodoScreens {
    const val TASKS_SCREEN = "tasks"
    const val STATISTICS_SCREEN = "statistics"
    const val TASK_DETAIL_SCREEN = "task"
    const val ADD_EDIT_TASK_SCREEN = "addEditTask"
}

object TodoDestinationArgs {
    const val USER_MESSAGE_ARG = "userMessage"
    const val TASK_ID_ARG = "taskId"
    const val TITLE_ARG = "title"
}

object TodoDestinations {
    const val TASKS_ROUTE = "$TASKS_SCREEN?$USER_MESSAGE_ARG = $USER_MESSAGE_ARG"
    const val STATISTICS_ROUTE = STATISTICS_SCREEN
    const val TASK_DETAIL_ROUTE = "$TASK_DETAIL_SCREEN/$TASK_ID_ARG"
    const val ADD_EDIT_TASK_ROUTE = "$ADD_EDIT_TASK_SCREEN/$TITLE_ARG?$TASK_ID_ARG=$TASK_ID_ARG"
}

class TodoNavigationActions(private val navController: NavHostController) {

    fun navigationToTasks(userMessage: Int = 0) {
        val navigatesFromDrawer = userMessage == 0
        navController.navigate(
            TASKS_SCREEN.let {
                if (userMessage != 0) {
                    "$it?${USER_MESSAGE_ARG}=$userMessage"
                } else {
                    it
                }
            }
        ) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = !navigatesFromDrawer
                saveState = navigatesFromDrawer
            }
            launchSingleTop = true
            restoreState = navigatesFromDrawer
        }
    }

    fun navigateToStatistics(){
        navController.navigate(STATISTICS_SCREEN){
            popUpTo(navController.graph.findStartDestination().id){
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToTaskDetail(taskId:String){
        navController.navigate("$TASK_DETAIL_SCREEN/$taskId")
    }

    fun navigateToAddEditTask(title:Int,taskId:String?){
        navController.navigate(
            "$ADD_EDIT_TASK_SCREEN/$title".let{
                if(taskId!=null){
                    "$it?$TASK_ID_ARG = $taskId"
                }else{
                    it
                }
            }
        )
    }
}