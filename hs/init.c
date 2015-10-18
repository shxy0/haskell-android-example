#include <stdio.h>
#include <jni.h>
//#include <HsFFI.h>
#include <Rts.h>
#include <RtsAPI.h>

#include <android/log.h>


#include <unistd.h>
#include <sys/types.h>
#include <errno.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>

#include "uthash.h"

extern void __stginit_Client(void);

extern void chatter( void );

void (*fptr_callback)(char*, int, char*, int);

void register_callback_fptr ( void (*v)(char*, int, char*, int) ) {
  fptr_callback = v;
}

static JavaVM* jvm; 

pthread_t thr_haskell;
pthread_t thr_msgread; 
pthread_t thr_msgwrite;

pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t  cond = PTHREAD_COND_INITIALIZER;

pthread_mutex_t wlock = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t wcond = PTHREAD_COND_INITIALIZER;

int size_nickbox =0;
char nickbox[4096];

int size_messagebox = 0;
char messagebox[4096]; 

int size_wmessage = 0;
char wmessage[4096];

//JNIEnv* ref_env;
//jobject ref_act; 

int activityId;
jclass ref_class;
jmethodID ref_mid;

struct my_jobject {
  int id;
  jobject ref;
  UT_hash_handle hh;
};

struct my_jobject *ref_objs = NULL;


void prepareJni( JNIEnv* env ) {
  jclass cls = (*env)->FindClass(env,"com/uphere/vchatter/Chatter"); 
  if( cls ) {
    ref_mid = (*env)->GetMethodID(env, cls, "sendMsgToChatter", "([B)V");
    (*env)->DeleteLocalRef(env,cls);
  }
}


void callJniTest( JNIEnv* env, char* cmsg, int n )
{
  // jstring jmsg = (*env)->NewStringUTF(env,cmsg);
  jbyteArray jmsg = (*env)->NewByteArray(env,n);
  (*env)->SetByteArrayRegion(env,jmsg,0,n,(jbyte*)cmsg);
  struct my_jobject *s; 

  HASH_FIND_INT( ref_objs, &activityId, s );
  if( s ) {
    (*env)->CallVoidMethod(env,s->ref,ref_mid,jmsg);
    
  }
  else {
    __android_log_write(3, "UPHERE", "NON EXIST");
  }

}  


void* haskell_runtime( void* d )
{
  static char *argv[] = { "libhaskell.so", 0 }, **argv_ = argv;
  static int argc = 1;
  static RtsConfig rtsopts = { RtsOptsAll, "-H128m -K64m" };
  // hs_init(&argc, &argv_);
  hs_init_ghc(&argc,&argv_, rtsopts);
  chatter();
  return NULL;
}

void* reader_runtime( void* d )
{
  JNIEnv* env;
  JavaVMAttachArgs args;
  args.version = JNI_VERSION_1_6;
  args.name = NULL;
  args.group = NULL;
  (*jvm)->AttachCurrentThread(jvm,(void**)&env, &args);
  while( 1 ) {
    pthread_mutex_lock(&lock);
    pthread_cond_wait(&cond,&lock);
    pthread_mutex_unlock(&lock);

    //size_t m = strlen(nickbox); 
    //size_t n = strlen(messagebox);
    
    fptr_callback(nickbox,size_nickbox,messagebox,size_messagebox);
  }
  return NULL;
}

void* writer_runtime( void* d )
{
  JNIEnv* env;
  JavaVMAttachArgs args;
  args.version = JNI_VERSION_1_6;
  args.name = NULL;
  args.group = NULL;
  (*jvm)->AttachCurrentThread(jvm,(void**)&env, &args);
  while( 1 ) {
    pthread_mutex_lock(&wlock);
    pthread_cond_wait(&wcond,&wlock);
    callJniTest( env, wmessage, size_wmessage );
    pthread_cond_signal(&wcond);
    pthread_mutex_unlock(&wlock);
    
  }
  return NULL;
}

JNIEXPORT jint JNICALL JNI_OnLoad( JavaVM *vm, void *pvt ) {
  jvm = vm;
  JNIEnv* env;
  (*jvm)->GetEnv(jvm, (void **)&env, JNI_VERSION_1_6);
  prepareJni(env);
  
  pthread_create( &thr_haskell, NULL, &haskell_runtime, NULL );
  
  return JNI_VERSION_1_6;
} 

JNIEXPORT void JNICALL JNI_OnUnload( JavaVM *vm, void *pvt ) {
  hs_exit();
  pthread_cond_destroy(&cond);
  pthread_mutex_destroy(&lock);
  pthread_cond_destroy(&wcond);
  pthread_mutex_destroy(&wlock);
  
  JNIEnv* env ;
  (*vm)->GetEnv(vm,(void**)(&env),JNI_VERSION_1_6);

  struct my_jobject *s, *tmp;

  HASH_ITER(hh, ref_objs, s, tmp ) {
    HASH_DEL( ref_objs, s );
    (*env)->DeleteGlobalRef(env,s->ref);
    free(s);
  }
} 

void
Java_com_uphere_vchatter_Chatter_onCreateHS( JNIEnv* env, jobject activity, jint k)
{
  activityId = k;
  pthread_create( &thr_msgread, NULL, &reader_runtime, NULL );
  pthread_create( &thr_msgwrite, NULL, &writer_runtime, NULL );
}


Java_com_uphere_vchatter_ObjectRegisterer_registerJRef( JNIEnv* env, jobject obj, jint k, jobject v )
{
  jobject ref = (*env)->NewGlobalRef(env,v);
  struct my_jobject *s ;
  s = malloc(sizeof(struct my_jobject));
  s->id = k ;
  s->ref = ref; 
  HASH_ADD_INT( ref_objs, id, s );
}
  
void
Java_com_uphere_vchatter_VideoFragment_onCreateHS( JNIEnv* env, jobject f,
						   jint k, jobject tv )
{
  jobject ref  = (*env)->NewGlobalRef(env,tv);
  struct my_jobject *s ;
  s = malloc(sizeof(struct my_jobject));
  s->id = k ;
  s->ref = ref; 
  HASH_ADD_INT( ref_objs, id, s );
}

void
Java_com_uphere_vchatter_VideoFragment_onClickHS( JNIEnv* env, jobject f,
				     	          jbyteArray nick, jbyteArray msg)
{
  char* cnick = (*env)->GetByteArrayElements(env,nick,NULL);
  char* cmsg  = (*env)->GetByteArrayElements(env,msg,NULL);
  pthread_mutex_lock(&lock);
  strcpy( messagebox , cmsg);
  strcpy( nickbox, cnick);
  size_nickbox = (*env)->GetArrayLength(env,nick);
  size_messagebox = (*env)->GetArrayLength(env,msg);
  pthread_cond_signal(&cond);
  pthread_mutex_unlock(&lock);
  (*env)->ReleaseByteArrayElements(env,msg,cmsg,NULL);
  (*env)->ReleaseByteArrayElements(env,nick,cnick,NULL);
}

void write_message( char* cmsg, int n )
{
  pthread_mutex_lock(&wlock);
  strcpy( wmessage , cmsg);
  size_wmessage = n;
  pthread_cond_signal(&wcond);
  pthread_cond_wait(&wcond,&wlock);
  pthread_mutex_unlock(&wlock);
}
 
