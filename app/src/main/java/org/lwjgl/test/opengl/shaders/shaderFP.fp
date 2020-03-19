!!ARBfp1.0
OPTION ARB_precision_hint_fastest;

ATTRIB winPos			= fragment.position;
ATTRIB iDots			= fragment.texcoord[0];

PARAM ambience			= state.lightmodel.ambient;

PARAM specularColor		= state.light[0].specular;

PARAM UNIFORMS			= program.local[0];

TEMP temp;

OUTPUT oColor			= result.color;

# Offset window-space fragment position.
ADD		temp.xyz, winPos, UNIFORMS.zwxx;
# Normalize position.
DP3		temp.w, temp, temp;
RSQ		temp.w, temp.w;
MUL		temp.xy, temp, temp.w;

# Multiply with current sin.
MUL		temp.xy, temp, UNIFORMS.x;
# {-1..1} => {0..1}
MAD		temp.xy, temp, 0.5, 0.5;
# Intensify colors.
MUL		temp.xy, temp, 2.0;
MOV		temp.z, 1.0;

# Accumulate color contributions.
MAD		temp.xyz, iDots.x, temp, ambience;
# Calculate <specular dot product>^<specular exponent>
POW		temp.w, iDots.y, UNIFORMS.y;
MAD		oColor.xyz, temp.w, specularColor, temp;

MOV		oColor.w, 1.0;

END