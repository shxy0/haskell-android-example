module Paths_HaskellActivity (
    version,
    getBinDir, getLibDir, getDataDir, getLibexecDir,
    getDataFileName, getSysconfDir
  ) where

import qualified Control.Exception as Exception
import Data.Version (Version(..))
import System.Environment (getEnv)
import Prelude

catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
catchIO = Exception.catch

version :: Version
version = Version [0,1,0,0] []
bindir, libdir, datadir, libexecdir, sysconfdir :: FilePath

bindir     = "/home/androidbuilder/.cabal/bin"
libdir     = "/home/androidbuilder/.cabal/lib/arm-linux-android-ghc-8.0.1/HaskellActivity-0.1.0.0-FKqya982D1FGv04P0wVORp"
datadir    = "/home/androidbuilder/.cabal/share/arm-linux-android-ghc-8.0.1/HaskellActivity-0.1.0.0"
libexecdir = "/home/androidbuilder/.cabal/libexec"
sysconfdir = "/home/androidbuilder/.cabal/etc"

getBinDir, getLibDir, getDataDir, getLibexecDir, getSysconfDir :: IO FilePath
getBinDir = catchIO (getEnv "HaskellActivity_bindir") (\_ -> return bindir)
getLibDir = catchIO (getEnv "HaskellActivity_libdir") (\_ -> return libdir)
getDataDir = catchIO (getEnv "HaskellActivity_datadir") (\_ -> return datadir)
getLibexecDir = catchIO (getEnv "HaskellActivity_libexecdir") (\_ -> return libexecdir)
getSysconfDir = catchIO (getEnv "HaskellActivity_sysconfdir") (\_ -> return sysconfdir)

getDataFileName :: FilePath -> IO FilePath
getDataFileName name = do
  dir <- getDataDir
  return (dir ++ "/" ++ name)
