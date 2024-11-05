##########################################################################################
#
# todo 第三方全局去掉混淆设置
# 这里相当于指定需要混淆的包
# 例子: -keep class !me.skean.framework.example.**, !com.blankj.**, ** {*;}
#
##########################################################################################
-keep class !me.skean.framework.example.** {*;}
-keep enum !me.skean.framework.example.** {*;}
-dontwarn **

#############################################
#
# todo 混淆中需要排除掉的设置
# 比例项目中需要保留的类, 如数据库Entity, Json的Bean, EventBus类
#
#############################################
#启动器
-keep class me.skean.framework.example.component.AppActivityLauncher {*;}
#数据库实体
-keep class me.skean.framework.example.db.entity.** {*;}
#数据库实体
-keep class me.skean.framework.example.db.dto.** {*;}
#eventbus传递的类
-keep class me.skean.framework.example.event.** {*;}
#网络请求实体
-keep class me.skean.framework.example.net.bean.** {*;}
