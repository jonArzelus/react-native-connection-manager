using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Connection.Manager.RNConnectionManager
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNConnectionManagerModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNConnectionManagerModule"/>.
        /// </summary>
        internal RNConnectionManagerModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNConnectionManager";
            }
        }
    }
}
