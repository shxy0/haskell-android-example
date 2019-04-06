
{-# LANGUAGE EmptyDataDecls #-}
{-# LANGUAGE ForeignFunctionInterface #-}
{-# LANGUAGE OverloadedStrings #-}

module HaskellActivity 
where

import System.IO

import Control.Monad
import Control.Monad.IO.Class

import GHC.Conc
import Control.Concurrent

import Data.Text (Text, append, pack)
import qualified Data.Text.IO as TIO

import Data.Foldable
import Data.IORef

-------------------------------------------------------------------------------81

import Foreign.C.String
import Foreign.Ptr

-- import Foreign.JNI
-- import Foreign.JNI.Lookup

data JObjectObj
type JObject = Ptr JObjectObj

data JNINativeInterface_
type JNIEnv = Ptr (Ptr JNINativeInterface_)

-------------------------------------------------------------------------------81

foreign export ccall
  "Java_com_example_hellojni_HelloJni_onCreateHS"
  onCreate :: JNIEnv -> JObject -> JObject -> IO ()

onCreate :: JNIEnv -> JObject -> JObject -> IO ()
onCreate env activity tv =  
  do
    getNumProcessors >>= setNumCapabilities
    caps <- getNumCapabilities

    let txt  = "\nMESSAGE FROM HASKELL:\n  Running on " ++ show caps ++ " CPUs!\n\n"
    -- cstr <- newCString txt

    -- tid <- myThreadId
    -- forkIO $
    iref <- newIORef 0
     
    -- fptr <- sampleFunPtr (testfun iref)
    -- saveFptr fptr
    mkOnClickFPtr (onClick iref) >>= registerOnClickFPtr
    
    ref <- newIORef []

    forkIO . sequenceA_ .
      replicate 10 $
      do
        threadDelay 10000
        modifyIORef' ref ('c':)

    threadDelay 1000000
    
    sequenceA_ .
      replicate 10 $
      do
        threadDelay 10000
        modifyIORef' ref ('d':)       

    str <- readIORef ref 
      
    cstr1 <- newCString $ txt ++ str
    shout env cstr1

    textViewSetText env tv cstr1

-------------------------------------------------------------------------------81

-- foreign export ccall
  -- "Java_com_example_hellojni_HelloJni_onClickHS"
  -- onClick :: IORef Int -> JNIEnv -> JObject -> JObject -> IO ()

onClick :: IORef Int -> JNIEnv -> JObject -> JObject -> IO ()
onClick ref env activity tv = 
  do
    n <- readIORef ref
    writeIORef ref (n+3)
    cstr <- newCString (show n) -- "CLICKED"
    shout env cstr
    textViewSetText env tv cstr
  
-------------------------------------------------------------------------------81

foreign import ccall "wrapper" mkOnClickFPtr
  :: (JNIEnv -> JObject -> JObject -> IO ()) 
  -> IO (FunPtr (JNIEnv -> JObject -> JObject -> IO ()))

foreign import ccall "c_register_on_click_fptr"
   registerOnClickFPtr :: FunPtr (JNIEnv -> JObject -> JObject -> IO ()) -> IO ()



foreign import ccall "shout" shout :: JNIEnv -> CString -> IO ()

foreign import ccall "c_textView_setText" textViewSetText :: JNIEnv -> JObject -> CString -> IO ()


{-                              
foreign export ccall
  "Java_com_example_hellojni_HelloJni_stringFromJNI"
  stringFromJNI :: JNIEnv -> JObject -> IO JString


stringFromJNI :: JNIEnv -> JObject -> IO JString
stringFromJNI env _ = runJNI env $ do
                        jstr <- newString "MESSAGE FROM HASKELL: HELLO WORLD!"
                        return jstr

-}
