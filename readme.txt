FFSMProcessor is an eclipse project that you can import in some clean workspace (ex. neon 2). You can explore some files that I used to set up and run my experiments;

The file "eclipse_neon2_linux_ffsm.tar.gz" is a Linux package with the whole environment that I used to code using the Yakindu project.
https://drive.google.com/file/d/1F-kS_ScYE3-I1qGWU92mc3GbYZR13b-P/view?usp=sharing 

Ignore my first commit as it is outdated and I uploaded a version that only works in my machine. So use "eclipse_neon2_linux_ffsm.tar.gz" above.

I tested in Linux mint 19 and ubuntu 20 and it was running ok as I put the correct java JDK inside the eclipse folder. 
I had errors to load eclipse with java JDK above number 8. (7 is for plugins and 8 for IDE and some plugins)
Once you extract the eclipse package just run "eclipse" and a logo "eclipse neon 2" should appear. 
Once you open it click File->Switch Workspace and change it to the folder "work_neon_ubu"
Then you should see several Yakindu plugins and one for br.icmc.ffsm with the source code adapted to run in the editor.
By default, it should appear the plugin "org.yakindu.sct.ui.editor" open then click "Launch an Eclipse application". 
Once you launch it another eclipse IDE should start to load but this time with all Yakindu and FFSM plugins loaded. 
In this new eclipse, another workspace should appear with all examples for AGM, CAS, and BCS.
I closed most of the projects to not take too much time checking all FFSMs.
Once you open a closed project, especially large ones such as BCS with several states you may notice some FFSM validation running in the console
of the first eclipse that you open with the source code.
Then you can explore FFSM models, feature models, and derived files. 

I am going to link a short video that I show these examples. It may have some bugs... its alpha and I did not updated it yet.
https://youtu.be/qqCQ-Vxhiqo
