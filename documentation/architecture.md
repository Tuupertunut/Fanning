# Architecture

![uml diagram](uml.png)

<!-- [FanningService]-1>[HardwareManager], [HardwareManager]-1>[HardwareItem], [HardwareItem]-*>[HardwareItem], [HardwareItem]-*>[Sensor], [HardwareItem]-*>[Control], [Control]-1>[Sensor], [FanningService]-*>[FanCurve], [FanCurve]-1>[Sensor], [FanCurve]-1>[Control], [HardwareItem]-.->[<<interface>>;HardwareTreeElement], [Sensor]-.->[<<interface>>;HardwareTreeElement], [Control]-.->[<<interface>>;HardwareTreeElement] -->