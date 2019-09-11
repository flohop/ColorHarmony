package com.florian.colorharmony_theory_stragety.geom.mesh.subdiv;

import com.florian.colorharmony_theory_stragety.geom.Vec3D;

import java.util.List;

public interface NewSubdivStrategy {

    public List<Vec3D[]> subdivideTriangle(Vec3D a, Vec3D b, Vec3D c,
                                           List<Vec3D[]> resultVertices);
}
